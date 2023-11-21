import { test, expect, Page } from '@playwright/test';
import { faker } from '@faker-js/faker';

async function loginEmpresa(page: Page) {
  await page.goto('https://jobs.abc.muniter.link/es/company/login');
  await page.getByLabel('Correo electrónico').fill('empresa1@email.com');
  await page.getByLabel('Contraseña').fill('123456789');
  await page.getByRole('button', { name: 'INICIAR SESIÓN' }).click();
  // Wait for page redirect
  await page.waitForURL(/.*es.company.home.*/);
}

test('login empresa', async ({ page }) => {
  await loginEmpresa(page);
  expect(page.url()).toContain('/es/company/home');
});

test('login empresa see people', async ({ page }) => {
  await loginEmpresa(page);
  await page.getByText('Personal').click();
  await expect(page.getByRole('heading', { name: 'Personal', exact: true })).toBeVisible();
  expect(page.url()).toContain('/es/company/people');

  // Test i18n, replace /es in url with /en
  await page.goto(page.url().replace('/es/', '/en/'));
  await expect(page.getByRole('heading', { name: 'People', exact: true })).toBeVisible();
})


test('vista personal y crear personal', async ({ page }) => {
  const fullName = faker.person.fullName();
  await loginEmpresa(page);
  await page.getByText('Personal').click();
  await page.getByText('add_circle').click();
  await page.getByPlaceholder('Nombres y apellidos').fill(fullName);
  await page.getByPlaceholder('Posición Actual').fill(faker.person.jobTitle());

  await page.getByPlaceholder('Tecnologías y aptitudes').click();
  await page.getByRole('option', { name: 'Software Engineer' }).locator('span').click();

  await page.getByText('Seleccionar tipo de').click();
  await page.getByRole('option', { name: 'The Inspector (ISTJ)' }).locator('span').click();

  // i18n
  await page.goto(page.url().replace('/es/', '/en/'));
  await page.getByText('Add Employee').first().isVisible();
  await page.getByText('People').first().first().isVisible();
})

test('vista equipo y crear equipo', async ({ page }) => {
  await loginEmpresa(page);
  await page.getByText('Equipos y Proyectos').click();
  await expect(page.getByRole('heading', { name: 'Crear Equipo' })).toBeVisible();
  await page.getByTitle('Crear Equipo').locator('mat-icon').click();
  await expect(page.getByPlaceholder('Nombre del equipo')).toBeVisible();
  await page.getByRole('button', { name: 'Cancelar' }).click();
  // i18n
  await page.goto(page.url().replace('/es/', '/en/'));
  await page.getByText('Teams and projects').first().isVisible();
  await page.getByText('New Team').first().isVisible();
})

test('vista proyecto y crear proyecto', async ({ page }) => {
  await loginEmpresa(page);
  await page.getByText('Equipos y Proyectos').click();
  await expect(page.getByRole('heading', { name: 'Crear Proyecto' })).toBeVisible();
  await page.getByTitle('Crear Proyecto').locator('mat-icon').click();
  await expect(page.getByPlaceholder('Nombre del proyecto')).toBeVisible();
  await page.getByRole('button', { name: 'Cancelar' }).click();
  // i18n
  await page.goto(page.url().replace('/es/', '/en/'));
  await page.getByText('Teams and projects').first().isVisible();
  await page.getByText('New Project').first().isVisible();
})

test('vista de vacantes y crear vacante', async ({ page }) => {
  await loginEmpresa(page);
  await page.getByText('Vacantes').click();
  await expect(page.getByRole('heading', { name: 'Crear Vacante' })).toBeVisible();
  await page.getByRole('heading', { name: 'Crear Vacante' }).click();
  await expect(page.getByPlaceholder('Nombre de la vacante')).toBeVisible();
  await expect(page.getByText('Seleccionar equipo')).toBeVisible();
  await page.getByRole('button', { name: 'Cancelar' }).click();
  // i18n
  await page.goto(page.url().replace('/es/', '/en/'));
  await page.getByText('Positions').first().isVisible();
  await page.getByText('New Position').first().isVisible();
});

test('vista de acciones vacantes', async ({ page }) => {

  await loginEmpresa(page);
  await page.getByText('Vacantes').click();
  await page.locator('app-team-project-card').nth(1).click();
  await expect(page.getByRole('button', { name: 'Cerrar', exact: true })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Cerrar Vacante' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Agendar Entrevista' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Guardar' })).toBeVisible();

  // i18n
  await page.goto(page.url().replace('/es/', '/en/'));
  await page.locator('app-team-project-card').nth(1).click();
  await expect(page.getByRole('button', { name: 'Close', exact: true })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Close position' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Schedule Interview' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Save' })).toBeVisible();
});


test('vista de busqueda de candidatos', async ({ page }) => {
  await loginEmpresa(page);
  await page.getByText('Candidatos').click();
  await page.getByRole('button', { name: 'BÚSQUEDA' }).click();
  await page.waitForTimeout(1000);
  await page.locator('.mat-mdc-form-field-infix').first().click();
  await page.getByRole('option', { name: 'Afghanistan' }).click();
  await page.getByRole('button', { name: 'Buscar' }).click();

  await page.getByText('País').first().isVisible();
  await page.getByText('Tecnologías').first().isVisible();

  // At least one candidate is visible
  await expect(page.getByText('Afghanistan').first()).toBeVisible();

  // Click one and see if we can pre-select
  await page.getByText('account_circle').nth(1).click();
  await expect(page.getByText('Vacante', { exact: true })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Cancelar' })).toBeVisible();
  await expect(page.locator('app-abc-button').filter({ hasText: 'Guardar' })).toBeVisible();
  await page.getByRole('button', { name: 'Cancelar' }).click();

  // i18n
  await page.goto(page.url().replace('/es/', '/en/'));
  await page.getByText('Candidates').first().click();
  await page.getByRole('button', { name: 'SEARCH' }).click();
  await page.waitForTimeout(1000);
  await page.getByText('Country').first().isVisible();
  await page.getByText('Technologies').first().isVisible();
});

test('vista contrataciones', async ({ page }) => {
  await loginEmpresa(page);
  await page.getByText('Contrataciones').click();
  await expect(page.getByRole('heading', { name: 'Personal', exact: true })).toBeVisible();

  // i18n
  await page.goto(page.url().replace('/es/', '/en/'));
  await page.getByText('Hirings').first().isVisible();
});
