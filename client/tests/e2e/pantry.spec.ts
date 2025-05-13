import { expect, test } from "@playwright/test";
import { Locator } from '@playwright/test';

//go to the page we are testing before each test
test.beforeEach(async ({page}) => {
  await page.goto("http://localhost:8000/");
});


test('test login to homescreen', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('button', { name: 'Pantry' }).click();
  await page.getByRole('button', { name: 'Receipt Scanner' }).click();
  await page.getByRole('button', { name: 'Recipe Finder' }).click();
  await page.getByRole('button', { name: 'Sign out' }).click();
});

test('test adding and removing an ingredients', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('textbox', { name: 'Enter an Ingredient' }).click();
  await page.getByRole('textbox', { name: 'Enter an Ingredient' }).fill('apple');
  await page.getByRole('textbox', { name: 'Enter Quantity' }).click();
  await page.getByRole('textbox', { name: 'Enter Quantity' }).fill('1');
  await page.getByRole('textbox', { name: 'Enter Expiration (MM/DD/YY)' }).click();
  await page.getByRole('textbox', { name: 'Enter Expiration (MM/DD/YY)' }).fill('01/01/26');
  await page.getByRole('button', { name: 'Submit' }).click();
  await page.getByRole('heading', { name: 'apple' }).click();
  await page.getByText('Expiration: 01/01/').click();
  await page.getByRole('checkbox', { name: 'Use in recipe' }).check();
  page.once('dialog', dialog => {
    console.log(`Dialog message: ${dialog.message()}`);
    dialog.dismiss().catch(() => {});
  });
  await page.getByRole('button', { name: '–' }).click();
  await page.getByRole('button', { name: 'Sign out' }).click();
});

test('test changing quantity', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByRole('button', { name: 'Sign in' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).click();
  await page.getByRole('textbox', { name: 'Email address' }).fill('kwisialo@cs.brown.edu');
  await page.getByRole('button', { name: 'Continue', exact: true }).click();
  await page.getByRole('textbox', { name: 'Password' }).fill('testcs0320');
  await page.getByRole('button', { name: 'Continue' }).click();
  await page.getByRole('textbox', { name: 'Enter an Ingredient' }).click();
  await page.getByRole('textbox', { name: 'Enter an Ingredient' }).fill('apple');
  await page.getByRole('textbox', { name: 'Enter Quantity' }).click();
  await page.getByRole('textbox', { name: 'Enter Quantity' }).fill('1');
  await page.getByRole('textbox', { name: 'Enter Expiration (MM/DD/YY)' }).click();
  await page.getByRole('textbox', { name: 'Enter Expiration (MM/DD/YY)' }).fill('01/01/26');
  await page.getByRole('button', { name: 'Submit' }).click();
  await page.getByRole('button', { name: '+' }).click();
  await page.getByRole('button', { name: '+' }).click();
  await page.getByRole('button', { name: '–' }).click();
  await page.getByRole('button', { name: '–' }).click();
  page.once('dialog', dialog => {
    console.log(`Dialog message: ${dialog.message()}`);
    dialog.dismiss().catch(() => {});
  });
  await page.getByRole('button', { name: '–' }).click();
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
});