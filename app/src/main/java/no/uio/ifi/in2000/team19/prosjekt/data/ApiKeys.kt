package no.uio.ifi.in2000.team19.prosjekt.data


/**
 * THIS IS NOT BEST PRACTICE AND IS A HUGE SECURITY RISK IF CODE IS MADE PUBLIC.
 *
 * But, we do this to make setup easier when sharing between team members and examiners. In a real scenario we would
 * find a better solution like storing them on a server so we are able to change them without hard updating the app.
 *
 * Again this is not what we would do outside of this project, but is done to avoid friction in handing over API keys.
 */
object ApiKeys {
    /** Please see ApiKeys for explanation */
    const val PROXY_KEY = "bfccf7dd-446a-4d37-9cf8-76e504f397b4"

    /** Please see ApiKeys for explanation */
    const val MAPBOX_ACCESS_TOKEN =
        "pk.eyJ1IjoibWFya3VzZXYiLCJhIjoiY2x0ZWFydGZnMGQyeTJpcnQ2ZXd6ZWdjciJ9.09_6aHo-sftYJs6mTXhOyA"

}