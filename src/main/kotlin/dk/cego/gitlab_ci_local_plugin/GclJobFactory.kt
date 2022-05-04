package dk.cego.gitlab_ci_local_plugin

class GclJobFactory {
    class HeaderLocations {
        val name = 0;
        var description = 0;
        var stage = 0;
        var glWhen = 0;
        var allowFailure = 0;
        var needs = 0;

    }
    fun parseJobLine(line: String, headerLocations: HeaderLocations): GclJob {
        val name = line.substring(headerLocations.name, headerLocations.description).trim()
        val description = line.substring(headerLocations.description, headerLocations.stage).trim()
        val stage = line.substring(headerLocations.stage, headerLocations.glWhen).trim()
        val glWhen = line.substring(headerLocations.glWhen, headerLocations.allowFailure).trim()
        val allowFailure = line.substring(headerLocations.allowFailure, minOf(headerLocations.needs,line.length)).trim().toBoolean()
        val needsStr = line.substring(minOf(headerLocations.needs,line.length)).trim().trimStart('[').trimEnd(']')
        val needs = needsStr.split(',').map { it.trim() }
        return GclJob(name, description, stage, glWhen, allowFailure, needs)
    }

    fun parseHeader(line: String): HeaderLocations {
        val headerLocations = HeaderLocations()
        headerLocations.description = line.indexOf("description")
        headerLocations.stage = line.indexOf("stage")
        headerLocations.glWhen = line.indexOf("when")
        headerLocations.allowFailure = line.indexOf("allow_failure")
        headerLocations.needs = line.indexOf("needs")
        return headerLocations
    }

    fun parse(fileContent: String): List<GclJob> {
        val lines = fileContent.lines()
        if(lines.count() < 3){
            return emptyList()
        }
        val headerLocations = parseHeader(lines[1])
        return lines.drop(2).filter { it.isNotBlank() }.map { parseJobLine(it, headerLocations) }
    }
}