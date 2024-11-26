package com.example.proyecto_kotlin
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
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
    private var _debug: Boolean = false;

    fun get_debug(): Boolean
    {
        return this._debug;
    }
    fun set_debug(_debug: Boolean)
    {
        this._debug = _debug;
    }

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


    //  Select
    fun consulta(_Con: Connection, _sql: String, vararg _parametros: Any): ResultSet?
    {
        val _ps: PreparedStatement = _Con.prepareStatement(_sql);

        return try
        {
            // Nota: Luego aprender ah hacer en un stream :)
            for ((i, param) in _parametros.withIndex())
            {
                when(param)
                {
                    is Int ->       _ps.setInt(i+1, param);
                    is String ->    _ps.setString(i+1, param);
                    is Double ->    _ps.setDouble(i+1, param);
                    is Float ->     _ps.setFloat(i+1, param);
                    is Date ->      _ps.setDate(i+1, param);
                    else -> throw IllegalArgumentException("Tipo no soportado por consulta; Tipo es: ${param::class}");
                }
            }

            val _setResultados = _ps.executeQuery();

            if (_debug)
                println(_setResultados.toString())

            _setResultados.close();
            _ps.close();
            _setResultados
        }
        catch(_sqle: SQLException)
        {
            println("Error en la consulta: ${_sqle.message}");
            null;
        }
        catch (_e: Exception)
        {
            println("Petardazo");
            null;
        }
    }


    // Update / Insert / Drop
    fun insertar(_Con: Connection, _sql: String, vararg _parametros: Any): Boolean
    {
        val _ps: PreparedStatement = _Con.prepareStatement(_sql);

        return try
        {
            // Nota: Luego aprender ah hacer en un stream :)
            for ((i, param) in _parametros.withIndex())
            {
                when(param)
                {
                    is Int ->       _ps.setInt(i+1, param);
                    is String ->    _ps.setString(i+1, param);
                    is Double ->    _ps.setDouble(i+1, param);
                    is Float ->     _ps.setFloat(i+1, param);
                    is Date ->      _ps.setDate(i+1, param);
                    else -> throw IllegalArgumentException("Tipo no soportado por consulta; Tipo es: ${param::class}");
                }
            }

            val columnas = _ps.executeUpdate()

            if (_debug)
                println(columnas)

            _ps.close();
            columnas > 0;
        }
        catch(_sqle: SQLException)
        {
            println("Error en la insercion: ${_sqle.message}");
            false;
        }
        catch (_e: Exception)
        {
            println("Petardazo");
            false;
        }
    }


    fun chapar(_Con: Connection): Boolean
    {
        if (_Con == null)
            return false;

        return try
        {
            _Con.close()
            true;
        }
        catch (_e: Exception)
        {
            println("Error: ${_e.message}");
            false;
        }
    }

}

/*
dependencies
{
    implementation("org.postgresql:postgresql:42.2.20")
}
*/

/*
fun ejecutarConsulta(connection: Connection, sql: String, vararg parametros: Any) {
    try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)

        // Asignar los valores de los parámetros varargs al PreparedStatement
        for ((index, parametro) in parametros.withIndex()) {
            when (parametro) {
                is Int -> preparedStatement.setInt(index + 1, parametro)
                is String -> preparedStatement.setString(index + 1, parametro)
                is Double -> preparedStatement.setDouble(index + 1, parametro)
                // Agrega más tipos según sea necesario
                else -> throw IllegalArgumentException("Tipo de parámetro no soportado: ${parametro::class}")
            }
        }

        val resultSet = preparedStatement.executeQuery()
        while (resultSet.next()) {
            println("ID: ${resultSet.getInt("id")}, Nombre: ${resultSet.getString("nombre")}")
        }

        resultSet.close()
        preparedStatement.close()
    } catch (e: SQLException) {
        println("Error al ejecutar la consulta: ${e.message}")
    }
}

fun main() {
    val conexion = Conexion("localhost", "5432", "mi_base_de_datos", "mi_usuario", "mi_contraseña")
    val connection = conexion.conectar()

    if (connection != null) {
        val sql = "SELECT * FROM usuarios WHERE id = ? AND nombre = ?"
        ejecutarConsulta(connection, sql, 1, "Juan")
        connection.close()
    } else {
        println("No se pudo conectar a la base de datos")
    }
}
*/