package contracts
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/internal/restaurants/outlets/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/organisation')), producer('/api/v1/internal/restaurants/outlets/123e4567-e89b-12d3-a456-426614174000/organisation')))
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            "outletId": "123e4567-e89b-12d3-a456-426614174000",
            "brandId": "123e4567-e89b-12d3-a456-426614174000",
            "organisationId": "321e4567-e89b-12d3-a456-426614174000"
        ])
    }
}
