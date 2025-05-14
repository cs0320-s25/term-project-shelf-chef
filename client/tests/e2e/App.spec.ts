import { expect, test } from "@playwright/test";
import { Locator } from '@playwright/test';
import { setupClerkTestingToken, clerk } from "@clerk/testing/playwright";

//go to the page we are testing before each test
test.beforeEach(async ({page}) => {
  await page.goto("http://localhost:8000/");
  setupClerkTestingToken({ page });
});


test('test login to homescreen', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
    await expect(page.getByText("Pantry")).toBeVisible();
    await expect(page.getByText("Receipt Scanner")).toBeVisible();
    await expect(page.getByText("Recipe Finder")).toBeVisible();
    await expect(page.getByText("Enter an Ingredient")).toBeVisible();
    await expect(page.getByText("Enter Quantity")).toBeVisible();
    await expect(page.getByText("Enter Expiration (DD/MM/YY)")).toBeVisible();
});

test('test changing ingredient quantity', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('button', { name: '+' }).first().click();
  await page.getByRole('button', { name: '–' }).first().click();
  await page.getByRole('button', { name: 'Sign out' }).click();
});


test('test ingredients persist across login/logout', async ({ page }) => {
  await page.goto('http://localhost:8000/');
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
  await page.getByRole('textbox', { name: 'Enter Expiration (MM/DD/YY)' }).click();
  await page.getByRole('textbox', { name: 'Enter Expiration (MM/DD/YY)' }).fill('01/01/25');
  await page.getByRole('button', { name: 'Submit' }).click();
  await page.getByRole('heading', { name: 'banana' }).click();
  await page.getByText('Expiration: 01/01/25').click();
  await page.getByRole('heading', { name: 'banana' }).click();
  await page.getByText('Expiration: 01/01/25').click();
  await page.getByRole('button', { name: 'Sign out' }).click();
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('heading', { name: 'banana' }).click();
  await page.getByText('Expiration: 01/01/25').click();
  await page.getByRole('button', { name: 'Sign out' }).click();
});

test('test receipt scanner page', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('button', { name: 'Receipt Scanner' }).click();
  await page.getByRole('heading', { name: 'Upload Receipt' }).click();
  await page.getByRole('button', { name: 'Upload' }).click();
  await page.getByRole('button', { name: 'Choose File' }).click();
  await page.getByRole('button', { name: 'Sign out' }).click();
});

test('test receipe page', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('button', { name: 'Recipe Finder' }).click();
  await page.getByRole('heading', { name: 'Selected Ingredients for' }).click();
  await page.getByRole('button', { name: 'Search for Recipe' }).click();
  await page.getByRole('heading', { name: 'Dietary Restrictions' }).click();
  await page.getByText('vegan').click();
  await page.getByText('vegetarian').click();
  await page.getByText('glutenFree').click();
  await page.getByText('dairyFree').click();
  await page.getByText('lowFODMAP').click();
  await page.getByText('Dietary Restrictions vegan').click();
  await page.getByRole('button', { name: 'Sign out' }).click();
});