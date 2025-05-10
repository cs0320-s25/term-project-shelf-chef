import { useState, useEffect } from "react";
import { useUser } from "@clerk/clerk-react";
import { addIngredient, deleteIngredient, fetchPantry } from "../utils/api";

interface Ingredient {
  name: string;
  quantity: string;
  expiration: string; // Format: MM/DD/YY
}

interface PantryProps {
selectedIngredients: string[];
setSelectedIngredients: React.Dispatch<React.SetStateAction<string[]>>;
}

export default function Pantry({ selectedIngredients, setSelectedIngredients }: PantryProps) {
  const [ingredient, setIngredient] = useState("");
  const [quantity, setQuantity] = useState("");
  const [expiration, setExpiration] = useState("");
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const { user } = useUser();

  const loadPantry = async () => {
    if (user?.id) {
      try {
        const fetchedIngredients = await fetchPantry(user.id);
        setIngredients(fetchedIngredients);
      } catch (err) {
        console.error("Error fetching pantry:", err);
      }
    }
  };

  useEffect(() => {
    loadPantry();
  }, [user?.id]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (ingredient.trim() && quantity.trim() && expiration.trim()) {
      const dateRegex = /^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[0-2])\/\d{2}$/;
      if (!dateRegex.test(expiration.trim())) {
        alert("Expiration date must be in DD/MM/YY format.");
        return;
      }
      const newIngredient: Ingredient = {
        name: ingredient.trim(),
        quantity: quantity.trim(),
        expiration: expiration.trim(),
      };
      setIngredient("");
      setQuantity("");
      setExpiration("");
      await addIngredient(
        user.id,
        newIngredient.name,
        newIngredient.quantity,
        newIngredient.expiration
      );

      await loadPantry();

      const formData = new FormData();
      formData.append("file", selectedFile);
      fetch("http://localhost:3600/receipt", {
        method: "POST",
        body: formData,
      })
        .then((response) => response.text())  // use .text() to see raw response
        .then((text) => {
        console.log(selectedFile)
        console.log("Raw response text:", text);
        const jsonData = JSON.parse(text); // manually parse so you can see the error
        return jsonData["success"];
      })
        .catch((error) => {
        console.log(error);
        }
      )
    }
  };

  const handleSelect = (ingredient: Ingredient) => {
    const isSelected = selectedIngredients.includes(ingredient.name);
  
    if (isSelected) {
      setSelectedIngredients(
        selectedIngredients.filter((name) => name !== ingredient.name)
      );
    } else {
      setSelectedIngredients([...selectedIngredients, ingredient.name]);
    }
  };

  const isExpired = (date: string) => {
    const today = new Date();
    const expDate = new Date(date);
    return expDate < today;
  };

  const handleDelete = async (indexToDelete: number) => {
    const deletingIngred = ingredients[indexToDelete];
    const updated = ingredients.filter((_, index) => index !== indexToDelete);
    setIngredients(updated);
    await deleteIngredient(user.id, deletingIngred.name, deletingIngred.quantity, deletingIngred.expiration)
  };

  return (
    <div className="App" style={{ padding: "20px" }}>
      <h1>Pantry Shelf</h1>

      <form onSubmit={handleSubmit} style={{ marginBottom: "20px" }}>
        <input
          type="text"
          value={ingredient}
          onChange={(e) => setIngredient(e.target.value)}
          placeholder="Enter an Ingredient"
          style={{
            padding: "8px",
            fontSize: "16px",
            marginRight: "8px",
            width: "250px",
          }}
        />
        <input
          type="text"
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
          placeholder="Enter Quantity"
          style={{
            padding: "8px",
            fontSize: "16px",
            marginRight: "8px",
            width: "250px",
          }}
        />
        <input
          type="text"
          value={expiration}
          onChange={(e) => setExpiration(e.target.value)}
          placeholder="Enter Expiration (MM/DD/YY)"
          style={{
            padding: "8px",
            fontSize: "16px",
            marginRight: "8px",
            width: "250px",
          }}
        />
        <button type="submit" style={{ padding: "8px 16px", fontSize: "16px" }}>
          Submit
        </button>
      </form>

      <div style={{ display: "flex", flexWrap: "wrap", gap: "20px" }}>
      {ingredients.map((ingredient, index) => {
        const isChecked = selectedIngredients.includes(ingredient.name);

        return (
          <div key={index} style={{
            border: "1px solid #ccc",
            borderRadius: "8px",
            padding: "16px",
            width: "220px",
            boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
            backgroundColor: isExpired(ingredient.expiration)
              ? "#ffd6d6" // red-ish if expired
              : "#f9f9f9",
          }}>
            <h3>{ingredient.name}</h3>
            <p>Quantity: {ingredient.quantity}</p>
            <p>Expiration: {ingredient.expiration}</p>
            <p>
            <label>
              <input
                type="checkbox"
                checked={isChecked}
                onChange={() => handleSelect(ingredient)}
              />
              Use in recipe
            </label>
            </p>
            <button onClick={() => handleDelete(index)} style={{ color: "red" }}>
              Delete
            </button>
          </div>
        );
      })}
      </div>

      <div style={{ display: "flex", flexWrap: "wrap", gap: "20px" }}>
        

      </div>
    </div>
  );
}