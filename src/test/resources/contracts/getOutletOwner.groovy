package contracts
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/internal/restaurants/outlets/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/owner')), producer('/api/v1/internal/restaurants/outlets/123e4567-e89b-12d3-a456-426614174000/owner')))
        headers {
            header('X-Calling-Service', value(consumer(regex('.*')), producer('communication-service')))
        }
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            "ownerId": "321e4567-e89b-12d3-a456-426614174000"
        ])
    }
}
