
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return internal order status")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/internal/restaurants/orders/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/status')), producer('/api/v1/internal/restaurants/orders/123e4567-e89b-12d3-a456-426614174000/status')))
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            status: 'PREPARING'
        ])
    }
}
