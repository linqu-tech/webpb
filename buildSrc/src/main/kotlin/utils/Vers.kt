package utils

import org.gradle.api.Project
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

object Vers {
    lateinit var commonsLang3: String
    lateinit var hibernateValidator: String
    lateinit var jackson: String
    lateinit var javaparser: String
    lateinit var jupiter: String
    lateinit var lombok: String
    lateinit var protobufJava: String
    lateinit var protoc: String
    lateinit var servletApi: String
    lateinit var springFramework: String
    lateinit var webpb: String

    fun initialize(project: Project) {
        this::commonsLang3.isInitialized
        this::class.memberProperties.forEach {
            if (it.isLateinit && it.javaField?.get(this) != null) {
                return
            }
            val key = it.name + "Version"
            if (project.hasProperty(key)) {
                val value = project.property(key)
                if (it is KMutableProperty<*>) {
                    it.setter.call(this, value)
                }
            }
        }
    }
}
