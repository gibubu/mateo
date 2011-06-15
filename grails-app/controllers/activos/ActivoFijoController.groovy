package activos

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_EMP'])
class ActivoFijoController {

    def index = { }
}
