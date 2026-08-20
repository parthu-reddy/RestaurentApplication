
import org.springframework.cloud.contract.spec.Contract

/*
 * Corrected 2026-08-20. This contract sent ids='id1,id2' and asserted id:'id1'. MenuItemDTO.id is a
 * UUID and CatalogController does UUID.fromString on each id, so 'id1' threw and the contract could
 * never pass.
 */
Contract.make {
    description("should return menu items batch")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/restaurants/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/menu/batch')), producer('/api/v1/restaurants/123e4567-e89b-12d3-a456-426614174000/menu/batch'))) {
            queryParameters {
                parameter 'ids': value(consumer(regex('.*')), producer('11111111-1111-1111-1111-111111111111,22222222-2222-2222-2222-222222222222'))
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
                id: '11111111-1111-1111-1111-111111111111',
                name: 'Pizza',
                price: 10.0
            ]
        ])
    }
}
