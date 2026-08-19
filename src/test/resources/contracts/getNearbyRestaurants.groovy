
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return nearby restaurants")
    request {
        method 'GET'
        urlPath('/api/v1/restaurants/nearby') {
            queryParameters {
                parameter 'lat': value(consumer(regex('-?\\d+(\\.\\d+)?')), producer('12.9716'))
                parameter 'lng': value(consumer(regex('-?\\d+(\\.\\d+)?')), producer('77.5946'))
                parameter 'radius': value(consumer(regex('\\d+(\\.\\d+)?')), producer('5.0'))
            }
        }
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            success: true,
            data: [
                [
                    id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
                    name: 'Test Restaurant',
                    rating: 4.5
                ]
            ]
        ])
    }
}
