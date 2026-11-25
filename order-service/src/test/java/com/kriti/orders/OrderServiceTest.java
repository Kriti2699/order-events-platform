//package com.kriti.orders;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class OrderServiceTest {
//
//    private final OrderRepository orderRepository = mock(OrderRepository.class);
//    private final OutboxEventRepository outboxRepository = mock(OutboxEventRepository.class);
//    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
//    private final OrderService service = new OrderService(orderRepository, outboxRepository, mapper);
//
//    @Test
//    void createOrder_savesOrderAndOutboxEventTogether() {
//        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        Order order = service.createOrder(new CreateOrderRequest("a@b.com", new BigDecimal("499.00")));
//
//        assertEquals(OrderStatus.CREATED, order.getStatus());
//
//        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
//        verify(outboxRepository).save(captor.capture());
//        OutboxEvent saved = captor.getValue();
//
//        assertEquals(order.getId(), saved.getAggregateId());
//        assertEquals("order.created", saved.getEventType());
//        assertFalse(saved.isPublished());
//        assertTrue(saved.getPayload().contains("a@b.com"));
//    }
//}
