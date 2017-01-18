CREATE TABLE resource (id INTEGER, address TEXT, title TEXT, PRIMARY KEY (id));
CREATE TABLE category (id INTEGER, name TEXT, parent INTEGER, PRIMARY KEY(id), FOREIGN KEY(parent) REFERENCES category(id));
CREATE TABLE resources_categories(resource INTEGER, category INTEGER, PRIMARY KEY (resource, category), FOREIGN KEY (resource) REFERENCES resource(id), FOREIGN KEY (category) REFERENCES category(id));
