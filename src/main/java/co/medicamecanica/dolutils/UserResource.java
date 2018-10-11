package co.medicamecanica.dolutils;

import org.restlet.resource.Get;

interface UserResource {
    @Get
    public User[] retrive();
}
