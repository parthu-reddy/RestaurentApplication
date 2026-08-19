
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return restaurant by id")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/restaurants/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}')), producer('/api/v1/restaurants/123e4567-e89b-12d3-a456-426614174000')))
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            name: 'Test Restaurant'
        ])
    }
}
