package node

open class SetRegistryTask : AbstractNodeExecTask() {
    override fun setupArguments(): Array<String> {
        return arrayOf(
            npm.get().absolutePath,
            "set",
            "registry",
            "https://${registry.get()}/"
        )
    }
}