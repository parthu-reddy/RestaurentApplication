package contracts
import org.springframework.cloud.contract.spec.Contract
Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/internal/restaurants/owner/00000000-0000-0000-0000-000000000000/outlets')
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([])
    }
}
