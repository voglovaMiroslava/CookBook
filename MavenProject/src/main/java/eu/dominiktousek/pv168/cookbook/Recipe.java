/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dominiktousek.pv168.cookbook;

import java.time.Duration;

/**
 *
 * @author Dominik
 */
public class Recipe {
    private Long id;
    private String name;
    private String instructions;
    private Duration duration;

    public Recipe(){}
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.instructions != null ? this.instructions.hashCode() : 0);
        hash = 29 * hash + (this.duration != null ? this.duration.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.instructions == null) ? (other.instructions != null) : !this.instructions.equals(other.instructions)) {
            return false;
        }
        if (this.duration != other.duration && (this.duration == null || !this.duration.equals(other.duration))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Recipe{" + "id=" + id + ", name=" + name + ", instructions=" + instructions + ", duration=" + duration + '}';
    }
}
