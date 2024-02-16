package {{project_base_package}}.samples.products

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(uniqueConstraints = [UniqueConstraint(name = "uk_code", columnNames = ["code"])])
data class Product(
    @field:NotBlank
    @Column(nullable = false, length = 13)
    val code: String,

    @field:NotBlank
    @field:Size(max = 120)
    @Column(nullable = false, length = 120)
    val name: String,

    @field:NotBlank
    @field:Size(max = 4000)
    @Column(nullable = false, length = 4000)
    val description: String
) {
    @Id
    @GeneratedValue
    var id: Long? = null
}