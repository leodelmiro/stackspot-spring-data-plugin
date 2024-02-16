package {{project_base_package}}.samples.products

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
@Transactional(readOnly = true)
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByCode(code: String?): Product?
}