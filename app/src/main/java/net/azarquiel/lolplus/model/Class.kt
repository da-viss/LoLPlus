package net.azarquiel.lolplus.model

import java.io.Serializable

data class Usuario (
    var nick: String,
    var champfav: String="noone",
    var posicionfav: String="",
    var finicio: String="",
    var estado: String=""
)

data class Campeon (
    var nombre: String="",
    var historia: String="",
    var rol: String="",
    var posicion: String="",
    var dificultad: String=""
):Serializable

data class Habilidad (
    var tecla: String,
    var nombre: String,
    var descripcion: String,
    var imagen: String
)

data class Comentario (
    var ncoment: Int,
    var usuario: Usuario,
    var fecha: String,
    var comentario: String
)