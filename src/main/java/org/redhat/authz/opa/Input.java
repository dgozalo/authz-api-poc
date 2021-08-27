package org.redhat.authz.opa;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public class Input {

    String subject;

    String relation;

    String namespace;

    String object;

    public Input(String subject, String relation, String namespace, String object) {
        this.subject = subject;
        this.relation = relation;
        this.namespace = namespace;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Input input = (Input) o;
        return Objects.equals(subject, input.subject) && Objects.equals(relation, input.relation) && Objects.equals(namespace, input.namespace) && Objects.equals(object, input.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, relation, namespace, object);
    }

    @Override
    public String toString() {
        return "Input{" +
                "subject='" + subject + '\'' +
                ", relation='" + relation + '\'' +
                ", namespace='" + namespace + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
