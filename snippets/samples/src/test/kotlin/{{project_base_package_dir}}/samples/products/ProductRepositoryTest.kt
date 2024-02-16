package {{project_base_package}}.samples.products

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.*
import org.assertj.core.api.InstanceOfAssertFactories.iterable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.TransactionSystemException


@SpringBootTest
@ActiveProfiles("test")
internal class ProductRepositoryTest {
    @Autowired
    private lateinit var repository: ProductRepository

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `should save a product`() {
        // scenario
        val product = Product("9788550800653", "Domain-Driven Design", "DDD - The blue book")

        // action
        repository.save(product)

        // validation
        assertThat(repository.findAll())
            .hasSize(1)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(product)
    }

    @Test
    fun `should not save a product with invalid parameters`() {
        // scenario
        val product = Product("97885-invalid", "a".repeat(121), "")

        // action and validation
        assertThatThrownBy { repository.save<Product>(product) }
            .isInstanceOf(TransactionSystemException::class.java)
            .hasRootCauseInstanceOf(ConstraintViolationException::class.java)
            .rootCause
            .extracting("constraintViolations", `as`(iterable(ConstraintViolation::class.java)))
            .extracting(
                { it.propertyPath.toString() }, { obj: ConstraintViolation<*> -> obj.message })
            .containsExactlyInAnyOrder(
                tuple("name", "size must be between 0 and 120"),
                tuple("description", "must not be blank")
            )
        // Tip: Try always to verify the side effects
        assertEquals(0, repository.count())
    }

    @Test
    fun `should not save a product when a product with same code already exists`() {
        // scenario
        val code = "9788550800653"
        val ddd = Product(code, "Domain-Driven Design", "DDD - The blue book")

        // action
        repository.save(ddd)

        // validation
        assertThrows(DataIntegrityViolationException::class.java) {
            val cleanCode = Product(code, "Clean Code", "Learn how to write clean code with Uncle Bob")
            repository.save(cleanCode)
        }
        // Tip: Try always to verify the side effects
        assertEquals(1, repository.count())
    }

    @Test
    fun `should find a product by code`() {
        // scenario
        val code = "9788550800653"
        val product = Product(code, "Domain-Driven Design", "DDD - The blue book")
        repository.save(product)

        // action
        val optionalProduct: Product? = repository.findByCode(code)

        // validation
        assertThat(optionalProduct)
            .usingRecursiveComparison()
            .isEqualTo(product)
    }

    @Test
    fun `should not find a product by code`() {
        // scenario
        val product = Product("9788550800653", "Domain-Driven Design", "DDD - The blue book")
        repository.save(product)

        // action
        val notExistingCode = "1234567890123"
        val optionalProduct: Product? = repository.findByCode(notExistingCode)

        // validation
        assertNull(optionalProduct)
    }
}