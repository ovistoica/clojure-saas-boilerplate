# Clojure SaaS Boilerplate

This is a boilerplate for Clojure SaaS applications. It is based on:

- [Integrant](https://github.com/weavejester/integrant) - System dependency management
- [Reitit](https://github.com/metosin/reitit) - Routing
- [AWS Cognito](https://aws.amazon.com/cognito/) - Authentication
- [PSQL](https://www.postgresql.org/) - Database
- [Shadow CLJS](https://github.com/thheller/shadow-cljs) - ClojureScript compilation (with hot reloading)
- [re-frame](https://github.com/) - Frontend framework

## Problems this boilerplate solves

There are many examples in the Clojure ecosystem but often times there is no place or codebase that keeps
all the pieces together. This boilerplate aims to solve that problem by providing a complete solution for building a
SaaS application that is truly production and enterprise ready.

## Features

- [x] Authentication
- [ ] Role based access control
- [x] User management - through the Cognito Console
- [x] Database migrations
- [x] Database seeding
- [ ] Database backups
- [ ] Database restores
- [ ] Database snapshots
- [ ] Frontend Web Application
- [ ] UI Design System
- [ ] UI Components
- [ ] UI Boilerplate pages
- [ ] Error handling
- [ ] Logging
- [ ] Monitoring
- [ ] Testing
- [ ] CI/CD
- [ ] Deployment
- [ ] Documentation
- [ ] Email templates
- [ ] Email sending

### Development

To start a development environment, run:

    $ make dev

This will start a REPL server on port 7000, and a Shadow CLJS server on port 8020.

### Production

To start a production environment, run:

    $ make prod

This will start a REPL server on port 7000, and a Shadow CLJS server on port 8020.