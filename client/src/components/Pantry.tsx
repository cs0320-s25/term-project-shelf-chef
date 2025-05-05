import { useState } from "react";
import "../styles/App.css";
import { useUser } from "@clerk/clerk-react";


export default function Pantry() {
    const [inputValue, setInputValue] = useState("");
    const [ingredients, setIngredients] = useState<string[]>([]);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const { user } = useUser();
  
    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();
      if (inputValue.trim() !== "") {
        setIngredients([...ingredients, inputValue.trim()]);
        setInputValue(""); // Clear input after submitting
        fetch(`http://localhost:3600/addPantry?userid=${user.id}&name=${inputValue}&quantity=1&expiration=12/31/25`).then((response) => response.json()).then(
            (jsonData) => {
              return jsonData["success"];
            }
          ).catch((error) => {
              console.error(error);
            }
          )
      }
    };
  
    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        setSelectedFile(file);
      }
    };
  
    const handleFileUpload = async () => {
      if(!selectedFile) {
        return;
      }
      fetch(`http://localhost:3600/receipt?file=${selectedFile}`).then((response) => response.json()).then(
        (jsonData) => {
          return jsonData["success"];
        }
      ).catch((error) => {
        console.error("error scanning receipt");
        }
      )
    }
    return (
      <div className="App" style={{ padding: "20px" }}>
        <h1>Ingredients</h1>
  
        <form onSubmit={handleSubmit} style={{ marginBottom: "20px" }}>
          <input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder="Enter an ingredient"
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
  
        <div style={{ marginBottom: "20px" }}>
          <input type="file" accept=".txt,.csv,.pdf" onChange={handleFileChange} />
          <button
            onClick={handleFileUpload}
            style={{ marginLeft: "10px", padding: "8px 16px", fontSize: "16px" }}
          >
            Upload Receipt
          </button>
        </div>
  
        <ul>
          {ingredients.map((ingredient, index) => (
            <li key={index} style={{ fontSize: "18px", marginBottom: "6px" }}>
              {ingredient}
            </li>
          ))}
        </ul>
      </div>
    );
  }