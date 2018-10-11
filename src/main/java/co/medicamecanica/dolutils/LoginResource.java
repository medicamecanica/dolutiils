package co.medicamecanica.dolutils;

import org.restlet.resource.Get;

interface LoginResource {
    @Get
    public Login login();

}
