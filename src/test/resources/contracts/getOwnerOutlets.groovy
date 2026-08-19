package contracts
import org.springframework.cloud.contract.spec.Contract
Contract.make {
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/internal/restaurants/owner/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/outlets')), producer('/api/v1/internal/restaurants/owner/123e4567-e89b-12d3-a456-426614174000/outlets')))
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            [
                id: "123e4567-e89b-12d3-a456-426614174001",
                name: "Outlet 1",
                address: "123 Main St",
                status: "OPEN"
            ]
        ])
    }
}
