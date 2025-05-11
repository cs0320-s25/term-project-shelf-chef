// ReceiptScanner.tsx
import { useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { addIngredient, deleteIngredient, fetchPantry, updateIngredientQuantity } from "../utils/api";

interface Ingredient {
  name: string;
  quantity: string;
  expiration: string; // Format: MM/DD/YY
}

export default function ReceiptScanner() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [confirmed, setConfirmed] = useState<boolean>(false);
  const { user } = useUser();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      setConfirmed(false);
    }
  };

  const handleFileUpload = async () => {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      const response = await fetch("http://localhost:3600/receipt", {
        method: "POST",
        body: formData,
      });
      const jsonData = await response.json();
      if (jsonData.success) {
        const entries: Ingredient[] = jsonData.success.map((item: string) => ({
          name: item,
          quantity: 1,
          expiration: "", // default to empty
        }));
        setIngredients(entries);
      }
    } catch (error) {
      console.error("Upload failed:", error);
    }
  };

  const handleQuantityChange = (index: number, value: string) => {
    const updated = [...ingredients];
    updated[index].quantity = value;
    setIngredients(updated);
  };

  const handleExpirationChange = (index: number, value: string) => {
    const updated = [...ingredients];
    updated[index].expiration = value;
    setIngredients(updated);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const dateRegex = /^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[0-2])\/\d{2}$/;

    for (const entry of ingredients) {
        const name = entry.name?.toString().trim();
        const quantity = entry.quantity?.toString().trim();
        const expiration = entry.expiration?.toString().trim();

      if (!name.trim() || !quantity.trim() || !expiration.trim()) {
       alert("All fields must be filled in for each ingredient.");
        return;
     }

      if (!dateRegex.test(expiration.trim())) {
        alert(`Invalid date format for ${name}. Use DD/MM/YY.`);
        return;
      }

      await addIngredient(
        user.id,
        name.trim(),
        quantity.trim(),
        expiration.trim()
      );
  }

  // Clear the list and reload pantry after submission
  setIngredients([]);
};

  return (
    <div className="App" style={{ padding: "20px" }}>
      <h1>Upload Receipt</h1>

      <input type="file" accept=".txt,.csv,.pdf" onChange={handleFileChange} />
      <button
        onClick={handleFileUpload}
        style={{ marginLeft: "10px", padding: "8px 16px", fontSize: "16px" }}
      >
        Upload
      </button>

      {ingredients.length > 0 && (
        <div style={{ marginTop: "20px" }}>
          <h2>Detected Ingredients</h2>
          {ingredients.map((item, index) => (
            <div key={index} style={{ marginBottom: "10px" }}>
              <strong>{item.name}</strong>
              <input
                type="number"
                min="1"
                value={item.quantity}
                onChange={(e) =>
                  handleQuantityChange(index, e.target.value)
                }
                style={{ marginLeft: "10px", width: "60px" }}
              />
               <input
                  type="text"
                  value={item.expiration}
                  onChange={(e) => handleExpirationChange(index, e.target.value)}
                  placeholder="Enter Expiration (MM/DD/YY)"
                  style={{
                  padding: "8px",
                  fontSize: "16px",
                  marginRight: "8px",
                  width: "250px",
                }}
              />
            </div>
          ))}
          <button
            onClick={handleSubmit}
            style={{
              marginTop: "15px",
              padding: "8px 16px",
              fontSize: "16px",
              backgroundColor: "#4CAF50",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            Confirm
          </button>
        </div>
      )}

      {confirmed && <p style={{ marginTop: "15px", color: "green" }}>Ingredients confirmed!</p>}
    </div>
  );
}