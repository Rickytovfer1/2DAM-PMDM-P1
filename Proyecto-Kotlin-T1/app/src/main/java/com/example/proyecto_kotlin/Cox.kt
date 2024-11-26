package com.example.proyecto_kotlin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Cox
(
    private val _host:      String,
    private val _puerto:    String,
    private val _usu:       String,
    private val _contra:    String,
    private val _BD:        String
)
{
    private var _url: String = "jdbc:postgresql://$_host:$_puerto/$_BD";

    fun conectar(): Connection?
    {
        return try
        {
            Class.forName("org.postgresql.Driver");
            DriverManager.getConnection(_url, _usu, _contra);
        }
        catch (_cnfe: ClassNotFoundException)
        {
            println("Error en Drivers de Postgres: ${_cnfe.message}");
            null;
        }
        catch (_sqle: SQLException)
        {
            println("Error al conectarse a la BD: ${_sqle.message}");
            null;
        }

    }

}

/*
dependencies
{
    implementation("org.postgresql:postgresql:42.2.20")
}
*/