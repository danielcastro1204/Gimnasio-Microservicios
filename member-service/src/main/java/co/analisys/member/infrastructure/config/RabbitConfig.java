package co.analisys.member.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

/**
 * Infraestructura de mensajería de member-service.
 *
 * 1) Pub/sub: solo declara el exchange topic compartido ("gym.events") y
 *    publica en él. No declara colas de ese exchange: son responsabilidad de
 *    quien consume (notification-service).
 *
 * 2) Cola de trabajo punto a punto "pagos-queue" (+ su Dead Letter Queue
 *    "pagos-dlq"): a diferencia del exchange topic, aquí SÍ se declaran las
 *    colas desde el publicador, porque member-service es tanto quien publica
 *    los pagos como quien los consume (PagoConsumer / PagoDeadLetterConsumer).
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange gymEventsExchange(@Value("${gym.events.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    /**
     * Cola principal de pagos. "x-dead-letter-exchange": "" + "x-dead-letter-routing-key":
     * "pagos-dlq" le dicen a RabbitMQ que, cuando un mensaje sea rechazado
     * (nack/reject) sin reencolar, lo mande directo a la cola "pagos-dlq"
     * usando el exchange por defecto.
     */
    @Bean
    public Queue pagosQueue() {
        return QueueBuilder.durable("pagos-queue")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "pagos-dlq")
                .build();
    }

    @Bean
    public Queue pagosDLQ() {
        return QueueBuilder.durable("pagos-dlq").build();
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    /**
     * Fábrica de listener con reintentos: cuando PagoConsumer.procesarPago lanza
     * una excepción, Spring Retry reintenta hasta 3 veces en el mismo proceso
     * (con backoff de 1s, 2s, 4s) ANTES de darse por vencido. Al agotar los
     * intentos, RejectAndDontRequeueRecoverer rechaza el mensaje sin
     * reencolarlo, y ahí es cuando RabbitMQ lo enruta a "pagos-dlq" (por los
     * argumentos de pagosQueue()). Esto cubre el requisito de la guía de
     * "reintentos antes de enviar a la DLQ" sin bloquear con Thread.sleep.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory pagosListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new org.springframework.retry.policy.SimpleRetryPolicy(3));
        org.springframework.retry.backoff.ExponentialBackOffPolicy backOffPolicy =
                new org.springframework.retry.backoff.ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000L);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(5000L);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        RetryOperationsInterceptor retryInterceptor = org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
                .stateless()
                .retryOperations(retryTemplate)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
        factory.setAdviceChain(retryInterceptor);

        return factory;
    }
}