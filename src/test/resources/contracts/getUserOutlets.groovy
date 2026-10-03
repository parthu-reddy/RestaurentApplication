package contracts
import org.springframework.cloud.contract.spec.Contract
Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/internal/restaurants/users/123e4567-e89b-12d3-a456-426614174000/outlets') {
            queryParameters { parameter('permission', 'ORG_VIEW') }
        }
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            "123e4567-e89b-12d3-a456-426614174000"
        ])
    }
}
