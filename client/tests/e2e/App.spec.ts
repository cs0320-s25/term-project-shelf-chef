import { expect, test } from "@playwright/test";
import { Locator } from '@playwright/test';
import { setupClerkTestingToken, clerk } from "@clerk/testing/playwright";

//go to the page we are testing before each test
test.beforeEach(async ({page}) => {
  await page.goto("http://localhost:8000/");
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
});



test('test login to homescreen', async ({ page }) => {
    await expect(page.getByText("Pantry")).toBeVisible();
    await expect(page.getByText("Receipt Scanner")).toBeVisible();
    await expect(page.getByText("Recipe Finder")).toBeVisible();
    await expect(page.locator('input[placeholder="Enter an Ingredient"]')).toBeVisible();
    await expect(page.locator('input[placeholder="Enter Quantity"]')).toBeVisible();
    await expect(page.locator('input[placeholder="Enter Expiration (DD/MM/YY)"]')).toBeVisible();
});

test('test adding and deleting ingredients persists across reloads', async ({ page }) => {  
  await page.getByRole('button', { name: '+' }).first().click({force: true});
    await expect(page.getByText("11")).toBeVisible();
  await page.getByRole('button', { name: '–' }).first().click({force: true});
    await expect(page.getByText("10")).toBeVisible();
  await page.getByRole('button', { name: 'Sign out' }).click();
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('textbox', { name: 'Enter an Ingredient' }).click();
  await page.getByRole('textbox', { name: 'Enter an Ingredient' }).fill('banana');
  await page.getByRole('textbox', { name: 'Enter Quantity' }).click();
  await page.getByRole('textbox', { name: 'Enter Quantity' }).fill('3');
  await page.locator('input[placeholder="Enter Expiration (DD/MM/YY)"]').click({force:true});
  await page.locator('input[placeholder="Enter Expiration (DD/MM/YY)"]').fill('01/01/25');
  await page.getByText("Submit").click({force: true});
  await page.waitForTimeout(2000);
    await expect(page.getByText("37")).toBeVisible();
    
});

test('ingredients that are expired display correctly', async ({ page }) => {
  const ingredientCard = page.locator('div', { hasText: 'banana' }).first();
  await expect(ingredientCard.locator('span[title="This ingredient is expired."]')).toBeVisible();
});

test('test receipt scanner page', async ({ page }) => {
  await page.getByRole('button', { name: 'Receipt Scanner' }).click();
  await page.locator('input[type="file"]').setInputFiles("/Users/ryanma05/Downloads/receipt_testing1.pdf");
  await page.getByRole('button', { name: 'Upload' }).click({ force: true });
  await page.waitForSelector('div.ingredient-list', { state: 'visible' });
  await expect(page.locator('div', { hasText: 'chicken' })).toBeVisible();
  await expect(page.getByText("oats")).toBeVisible();
  await expect(page.getByText("onion")).toBeVisible();
});

test('test receipe page', async ({ page }) => {
  const checkboxes = await page.locator('input[type="checkbox"]').all();

  for (const checkbox of checkboxes) {
    if (!(await checkbox.isChecked())) {
      await checkbox.check();
    }
  }
  await page.getByRole('button', { name: 'Recipe Finder' }).click();
  await page.getByRole('button', { name: 'Search for Recipe' }).click({force:true});
  await page.waitForTimeout(2000);
    await expect(page.getByText("Chilled Swiss Oatmeal")).toBeVisible(); //contains milk
  
  await page.getByText('vegan').click({force:true});
  await page.getByRole('button', { name: 'Search for Recipe' }).click({force:true});
  await page.waitForTimeout(2000);
    await expect(page.getByText("Chilled Swiss Oatmeal")).not.toBeVisible(); //should not be visible with vegan
    await expect(page.getByText("Vegan Banana Nut Muffins")).toBeVisible(); //vegan food should be visible
  
  await page.getByText('vegan').click({force:true}); //undo vegan
  await page.getByText('dairyFree').click({force:true}); 
    

});