package com.example.vertxVKR.handlers

import com.google.gson.Gson
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.util.FileManager
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

class MainHandler : Handler<RoutingContext> {

    init {
//        sparqlExec()
    }

    override fun handle(event: RoutingContext) {
        event.request().bodyHandler { requestBody ->
            try {
                endResponse(event, sparqlExec())
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun endResponse(event: RoutingContext, sparqlTest: Boolean) {
        val response = event.response()
        val resp = if (sparqlTest){
            "success"
        } else {
            "fail"
        }
        val jsonResponseBody = Gson().toJson(resp)
        response.end(jsonResponseBody)
    }

    private fun sparqlExec(): Boolean {
        FileManager.get().addLocatorClassLoader(MainHandler::class.java.classLoader)
        val file = FileManager.get().loadModel("C:\\Users\\Anton\\IdeaProjects\\art\\src\\main\\resources\\iot_vkr2.owl")

        val queryStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX iot: <http://webprotege.stanford.edu/project/co6WD6WelXsz2DLygYbnb#>\n" +
                "SELECT ?x\n" +
                "WHERE {\n" +
                "?x iot:измеряет iot:вибрация.\n" +
                "?x rdf:type ?x\n" +
                "}"

        val query = QueryFactory.create(queryStr)
        val qexec = QueryExecutionFactory.create(query, file)
        try {
            val results = qexec.execSelect()
            while (results.hasNext()) {
                val soln = results.nextSolution()
                System.out.println("x == " + soln.get("x").toString())
                System.out.println("")
            }

        } catch (e: Throwable) {
            System.out.println("error")
            return false
        } finally {
            qexec.close()
        }
        return true
    }

}