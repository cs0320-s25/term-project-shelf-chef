

export async function addIngredient(uid: string, title: string, quantity: string, date: string) {
  if (!uid || !title || !quantity || !date) {
    throw new Error("All fields (uid, title, quantity, date) are required.");
  }

  const url = `http://localhost:3600/addPantry?userid=${encodeURIComponent(uid)}&name=${encodeURIComponent(title)}&quantity=${quantity}&expiration=${encodeURIComponent(date)}`;
  console.log(url)
  const response = await fetch(url);

  if (!response.ok) {
    throw new Error(`Error ${response.status}: ${response.statusText}`);
  }

  const data = await response.json();
  return data["success"];
}

// export async function deleteIngredient(uid: string, name: string, quantity: string, expiration: string) {
//   //TODO
//   const response = await fetch(`http://localhost:3600/deletePantry`, {
//     method: "DELETE",
//     headers: { "Content-Type": "application/json" },
//     body: JSON.stringify({
//       userid: uid,
//       name,
//       quantity,
//       expiration,
//     }),
//   });

//   if (!response.ok) {
//     throw new Error(`Failed to delete ingredient: ${response.statusText}`);
//   }

//   return await response.json();
// }


export async function fetchPantry(userId: string) {
  const response = await fetch(
    `http://localhost:3600/addPantry?userid=${userId}&fetch=true`
  );
  const data = await response.json();
  console.log(data)
  return data.pantry
}

export async function updateIngredientQuantity(
  userId: string,
  name: string,
  expiration: string,
  quantity: string
) {
  const url = `http://localhost:3600/addPantry?userid=${encodeURIComponent(userId)}&name=${encodeURIComponent(name)}&expiration=${encodeURIComponent(expiration)}&quantity=${encodeURIComponent(quantity)}&update=true`;
  console.log(url)
  const response = await fetch(url);
  const data = await response.json();
  return data.status === "success";
}

export async function deleteIngredient(
  userId: string,
  name: string,
  expiration: string,
) {
  const url = `http://localhost:3600/addPantry?userid=${encodeURIComponent(userId)}&name=${encodeURIComponent(name)}&expiration=${encodeURIComponent(expiration)}&delete=true`;
  console.log(url)
  const response = await fetch(url);
  const data = await response.json();
  return data.status === "success";
}


export async function getRecipe(uid: string, ingredients: string[], dietaryRestrictions: string[]) {
  if (!uid || !ingredients) {
    throw new Error("User ID and Ingredients are required.");
  }

  const ingredParams = encodeURIComponent(ingredients.join(","));
  const dietParams = encodeURIComponent(dietaryRestrictions.join(","));

  let url = `http://localhost:3600/recipes?ingredients=${ingredParams}`;
  if (dietaryRestrictions.length > 0) {
    url += `&dietaryRestrictions=${dietParams}`;
  }
  console.log("URL:", url);

  const response = await fetch(url);

  if (!response.ok) {
    throw new Error(`Error ${response.status}: ${response.statusText}`);
  }

  const data = await response.json();
  console.log("API response:", data);

  if (data.response === "success") {
    return data.recipes;  
  } else {
    throw new Error(data.message || "Failed to fetch recipes");
  }
}