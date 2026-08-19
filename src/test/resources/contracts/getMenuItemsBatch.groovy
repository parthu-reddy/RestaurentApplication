
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return menu items batch")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/restaurants/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/menu/batch')), producer('/api/v1/restaurants/123e4567-e89b-12d3-a456-426614174000/menu/batch'))) {
            queryParameters {
                parameter 'ids': value(consumer(regex('.*')), producer('id1,id2'))
            }
        }
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            [
                id: 'id1',
                name: 'Pizza',
                price: 10.0
            ]
        ])
    }
}
