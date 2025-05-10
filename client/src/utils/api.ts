

export async function addIngredient(uid: string, title: string, quantity: string, date: string) {
  if (!uid || !title || !quantity || !date) {
    throw new Error("All fields (uid, title, quantity, date) are required.");
  }

  const url = `http://localhost:3600/addPantry?userid=${encodeURIComponent(uid)}&name=${encodeURIComponent(title)}&quantity=${quantity}&expiration=${encodeURIComponent(date)}`;

  const response = await fetch(url);

  if (!response.ok) {
    throw new Error(`Error ${response.status}: ${response.statusText}`);
  }
  const data = await response.json();
  return data["success"];
}

export async function deleteIngredient(uid: string, name: string, quantity: string, expiration: string) {
  //TODO
  const response = await fetch(`http://localhost:3600/deletePantry`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      userid: uid,
      name,
      quantity,
      expiration,
    }),
  });

  if (!response.ok) {
    throw new Error(`Failed to delete ingredient: ${response.statusText}`);
  }

  return await response.json();
}


export async function fetchIngredients(uid: string) {
//TODO
}


export async function getRecipe(uid: string, ingredients: string[], dietaryRestrictions: string[]) {
  if (!uid || !ingredients) {
    throw new Error("User ID and Ingredients are required.");
  }
  console.log("Entered getRecipeCall")

  const ingredParams = ingredients.join(",")
  const dietParams = dietaryRestrictions.join(",")
  console.log("ingredients:", ingredients)
  console.log("dietary:", dietaryRestrictions)

  
  if (dietaryRestrictions.length > 0) {
    console.log("Dietary Restrictions Search")
    const url = `http://localhost:3600/recipes?userid=${uid}&ingredients=${ingredParams}&dietaryRestrictions=${dietParams}`;
    console.log(url)
    const response = await fetch(url);
    console.log("GetRecipe Call Complete")
    if (!response.ok) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }
  
    const data = await response.json();
    return data["success"];
    
  } else {
    console.log("No Dietary Restrictions Search")
    const url = `http://localhost:3600/recipes?userid=${uid}&ingredients=${ingredParams}&dietaryRestrictions=${''}`;
    console.log(url)
    const response = await fetch(url);
    console.log(url)
    if (!response.ok) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }
  
    const data = await response.json();
    return data["success"];
  }
}

