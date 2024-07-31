package dk.cego.gitlabcilocal.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule


class GclJobFactory {

    fun parse(fileContent: String): List<GclJob> {
        // Skip until json array begins with [
        val jsonString = fileContent.substring(fileContent.indexOf('['))

        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.readValue<List<GclJob>>(jsonString)
    }
}