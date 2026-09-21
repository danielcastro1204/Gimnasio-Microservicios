package co.analisys.notification.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * notification-service es el suscriptor (pub/sub): declara el exchange topic
 * compartido "gym.events" y, a diferencia de los publicadores, también sus
 * propias colas y bindings, cada una con la routing key que le interesa.
 * Añadir un nuevo suscriptor a futuro NO requiere tocar member-service ni
 * class-service: basta con declarar una cola nueva enlazada al mismo exchange.
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange gymEventsExchange(@Value("${gym.events.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue memberRegisteredQueue(@Value("${notification.queues.member-registered}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue classEventsQueue(@Value("${notification.queues.class-events}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding memberRegisteredBinding(Queue memberRegisteredQueue, TopicExchange gymEventsExchange,
                                            @Value("${notification.routing-keys.member-registered}") String routingKey) {
        return BindingBuilder.bind(memberRegisteredQueue).to(gymEventsExchange).with(routingKey);
    }

    @Bean
    public Binding classEventsBinding(Queue classEventsQueue, TopicExchange gymEventsExchange,
                                       @Value("${notification.routing-keys.class-events}") String routingKey) {
        return BindingBuilder.bind(classEventsQueue).to(gymEventsExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        // Ignora el header __TypeId__ (referencia una clase del microservicio
        // publicador, que no existe aquí) e infiere el tipo destino a partir del
        // parámetro del método @RabbitListener: así ambos lados evolucionan sin
        // compartir un JAR de dominio.
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                                 MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}
