package dk.cego.gitlabcilocal.plugin

class GclStage (val stage: String, val jobs: ArrayList<GclJob>) {


    companion object {
        fun stagesFromJobs(jobs: List<GclJob>): List<GclStage> {
            // group jobs by stage
            val stages = mutableListOf<GclStage>()
            for (job in jobs) {
                var stage = stages.find { it.stage == job.stage }
                if (stage == null) {
                    stage = GclStage(job.stage, ArrayList())
                    stages.add(stage)
                }
                // add to list
                stage.jobs.add(job)
            }
            return stages
        }
    }
}
