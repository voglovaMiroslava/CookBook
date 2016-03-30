package eu.dominiktousek.pv168.cookbook;

/**
 *
 * @author Dominik
 */
public class IngredientAmount {
    private Long id;
    private Long recipeId;
    private Ingredient ingredient;
    private String amount;

    public IngredientAmount() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 71 * hash + (this.recipeId != null ? this.recipeId.hashCode() : 0);
        hash = 71 * hash + (this.ingredient != null ? this.ingredient.hashCode() : 0);
        hash = 71 * hash + (this.amount != null ? this.amount.hashCode() : 0);
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
        final IngredientAmount other = (IngredientAmount) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.recipeId != other.recipeId && (this.recipeId == null || !this.recipeId.equals(other.recipeId))) {
            return false;
        }
        if ((this.ingredient == null) ? (other.ingredient != null) : !this.ingredient.equals(other.ingredient)) {
            return false;
        }
        if ((this.amount == null) ? (other.amount != null) : !this.amount.equals(other.amount)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IngredientAmount{" + "id=" + id + ", recipeId=" + recipeId + ", ingredient=" + ingredient + ", amount=" + amount + '}';
    }
    
}
