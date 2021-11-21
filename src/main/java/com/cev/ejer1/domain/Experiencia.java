package com.cev.ejer1.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Experiencia.
 */
@Entity
@Table(name = "experiencia")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "experiencia")
public class Experiencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 5, max = 200)
    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Size(min = 5, max = 300)
    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Size(min = 5, max = 250)
    @Column(name = "localizacion", length = 250)
    private String localizacion;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Experiencia id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Experiencia titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Experiencia descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLocalizacion() {
        return this.localizacion;
    }

    public Experiencia localizacion(String localizacion) {
        this.setLocalizacion(localizacion);
        return this;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public Experiencia fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Experiencia)) {
            return false;
        }
        return id != null && id.equals(((Experiencia) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Experiencia{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", localizacion='" + getLocalizacion() + "'" +
            ", fecha='" + getFecha() + "'" +
            "}";
    }
}
