package utils

import org.gradle.api.Project
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

object Vers {
    private var initialized = false

    lateinit var checkstyle: String
    lateinit var commonsIo: String
    lateinit var commonsLang3: String
    lateinit var compileTesting: String
    lateinit var hibernateValidator: String
    lateinit var jackson: String
    lateinit var jacoco: String
    lateinit var javaparser: String
    lateinit var jupiter: String
    lateinit var lombok: String
    lateinit var mockito: String
    lateinit var protobufJava: String
    lateinit var protoc: String
    lateinit var reactorNetty: String
    lateinit var servletApi: String
    lateinit var springFramework: String
    lateinit var webpb: String

    fun initialize(project: Project) {
        if (initialized) {
            return
        }
        this.webpb = project.version.toString()
        this::class.memberProperties.forEach {
            val key = "version" + it.name.capitalize()
            if (project.hasProperty(key)) {
                val value = project.property(key)
                if (it is KMutableProperty<*>) {
                    it.setter.call(this, value)
                }
            }
        }
        initialized = true
    }
}
