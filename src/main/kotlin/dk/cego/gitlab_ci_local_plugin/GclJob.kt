package dk.cego.gitlab_ci_local_plugin

class GclJob (name: String, description: String, stage: String, glWhen: String, allow_failure: Boolean, needs: List<String>) {
    val name: String = name;
    private val description = description;
    val stage = stage;
    private val glWhen = glWhen;
    private val allow_failure = allow_failure
    private val needs = needs;
}