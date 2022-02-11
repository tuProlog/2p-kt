import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

apply<DetektPlugin>()

configure<DetektExtension> {
    // toolVersion = "1.19.0"
    config = rootProject.files(".detekt.yml")
    buildUponDefaultConfig = true
}
