package dk.cego.gitlab_ci_local_plugin

class GclJob(
    val name: String, private val description: String, val stage: String,
    private val glWhen: String, private val allow_failure: Boolean,
    private val needs: List<String>
)