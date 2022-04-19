package com.github.virgosoy.cvengineer.services

import com.intellij.openapi.project.Project
import com.github.virgosoy.cvengineer.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
