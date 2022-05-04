package dk.cego.gitlab_ci_local_plugin

class GclStage (stage: String, jobs: ArrayList<GclJob>) {
    val stage: String = stage
    val jobs: ArrayList<GclJob> = jobs
}
