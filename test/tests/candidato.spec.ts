import { test, expect, Page } from '@playwright/test';

async function loginCandidato(page: Page) {
  await page.goto('https://jobs.abc.muniter.link/es/candidate/login');
  await page.getByLabel('Correo electrónico').fill('candidato1@email.com');
  await page.getByLabel('Contraseña').fill('123456789');
  await page.getByRole('button', { name: 'INICIAR SESIÓN' }).click();
  // Wait for page redirect
  await page.waitForURL(/.*es.candidate.home.*/);
}

test('login candidato', async ({ page }) => {
  await loginCandidato(page);
  expect(page.url()).toContain('/es/candidate/home');
});

test('vista perfil', async ({ page }) => {
  await loginCandidato(page);
  await page.getByText('Perfil').click();
  await expect(page.getByRole('heading', { name: 'Información Académica' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Información Laboral' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Conocimientos Técnicos' })).toBeVisible();
})

test('vista perfil editar información personal', async ({ page }) => {
  await loginCandidato(page);
  await page.getByText('Perfil').click();
  const headingLocator = page.getByRole('heading', { name: 'Información Personal' })
  await page.getByRole('heading', { name: 'Información Personal' }).locator('mat-icon').click();
  // After the heading there's a form select it
  // await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  // await expect(page.getByRole('button', { name: 'Cancelar' })).toBeVisible();
});


test('vista perfil editar información académica', async ({ page }) => {
  await loginCandidato(page);
  await page.getByText('Perfil').click();
  await page.getByRole('heading', { name: 'Información Académica' }).locator('mat-icon').click();
  // await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  // await expect(page.getByRole('button', { name: 'Cancelar' })).toBeVisible();
  // await expect(page.getByRole('button', { name: 'AÑADIR OTRO' })).toBeVisible();
});

test('vista perfil editar información laboral', async ({ page }) => {
  await loginCandidato(page);
  await page.getByText('Perfil').click();
  await page.getByRole('heading', { name: 'Información Laboral' }).locator('mat-icon').click();
  // await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  // await expect(page.getByRole('button', { name: 'Cancelar' })).toBeVisible();
  // await expect(page.getByRole('button', { name: 'AÑADIR OTRO' })).toBeVisible();
});


test('vista perfil editar información técnica', async ({ page }) => {
  await loginCandidato(page);
  await page.getByText('Perfil').click();
  await page.getByRole('heading', { name: 'Conocimientos Técnicos' }).locator('mat-icon').click();
  await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Cancelar' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'AÑADIR OTRO' })).toBeVisible();
});
