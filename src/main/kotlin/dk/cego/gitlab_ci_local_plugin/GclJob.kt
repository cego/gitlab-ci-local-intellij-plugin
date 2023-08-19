package dk.cego.gitlab_ci_local_plugin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GclJob(
    val name: String,
    val description: String,
    val stage: String,
)