package no.uio.ifi.in2000.team19.prosjekt.model.dto

data class Advice(
    var title: String, // eks: "Kuldevarsel"
    var description: String, // eks: "Ikke gå tur med hunden uten gode klær, eller finn på aktiviteter inne"
    var shortAdvice: String
    //val color: String, // eks: #FFF000 - viser hvor viktig varsleen, gul = obs, rød = farlig
    //val forecast : AdviceForecast // for å temp, vind, tordenvarsel
)