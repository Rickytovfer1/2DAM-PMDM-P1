package com.example.proyecto_kotlin

class Cox
(
    _host:      String,
    _puerto:    String,
    _usu:       String,
    _contra:    String,
    _BD:        String
)
{
    lateinit var _host: String;
    lateinit var _puerto: String;
    lateinit var _usu: String;
    lateinit var _contra: String;
    lateinit var _BD: String;

    // conec-tar
    fun conec(): Boolean
    {

        return false;
    }

}

/*
dependencies
{
    implementation("org.postgresql:postgresql:42.2.20")
}
*/