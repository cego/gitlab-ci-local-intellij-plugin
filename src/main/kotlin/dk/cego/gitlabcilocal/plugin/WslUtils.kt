package dk.cego.gitlabcilocal.plugin

class WslUtils {
    companion object {
        private fun pathIsWsl(path: String): Boolean {
            return path.startsWith("//wsl$/")
        }

        private fun getWslDistro(path: String): String {
            return path.substring(7, path.indexOf("/", 7))
        }

        fun rewriteToWslExec(path: String, command: List<String>): List<String> {
            if(!pathIsWsl(path)) {
                return command
            }
            val distro = getWslDistro(path)
            val list = listOf("wsl", "-d", distro, "--", "cd", getWslPath(path, distro), "&&")
            return list + command
        }

        private fun getWslPath(path: String, distro: String): String {
            val indexOfPath = path.substring(7+distro.length)
            return indexOfPath.replace("\\", "/")
        }
    }



}