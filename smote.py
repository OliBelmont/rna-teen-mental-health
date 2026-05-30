import pandas as pd
import numpy as np

def smote(X, y, desired_ratio=0.20, k=5, random_state=42):
    np.random.seed(random_state)

    minority_idx = np.where(y == 1)[0]
    majority_idx = np.where(y == 0)[0]

    n_majority  = len(majority_idx)
    n_minority  = len(minority_idx)
    n_synthetic = int((desired_ratio * n_majority) / (1 - desired_ratio)) - n_minority

    print(f"Positivos originais : {n_minority}")
    print(f"Negativos           : {n_majority}")
    print(f"Sintéticos a gerar  : {n_synthetic}")

    minority_vals = X[minority_idx].astype(float)
    synthetic_X   = []

    for _ in range(n_synthetic):
        idx       = np.random.randint(0, n_minority)
        sample    = minority_vals[idx]
        diffs     = minority_vals - sample
        distances = np.sqrt((diffs ** 2).sum(axis=1))
        distances[idx] = np.inf
        neighbors  = np.argsort(distances)[:k]
        neighbor   = minority_vals[np.random.choice(neighbors)]
        gap        = np.random.random()
        synthetic_X.append(sample + gap * (neighbor - sample))

    synthetic_X = np.array(synthetic_X)
    synthetic_y = np.ones(len(synthetic_X))

    X_out = np.vstack([X, synthetic_X])
    y_out = np.concatenate([y, synthetic_y])

    # Embaralha
    idx = np.random.permutation(len(X_out))
    return X_out[idx], y_out[idx]


# Carrega dataset original
df = pd.read_csv("data/Teen_Mental_Health_Dataset.csv")

# Codifica categorias para número
cat_cols = ['gender', 'platform_usage', 'academic_performance', 'physical_activity', 'social_interaction_level']
encoders = {}
for col in cat_cols:
    codes, uniques = pd.factorize(df[col])
    df[col] = codes
    encoders[col] = uniques

# Separa X e y
X = df.drop(columns=['depression_label']).values.astype(float)
y = df['depression_label'].values.astype(float)

# Split treino/teste (80/20) com seed fixa
np.random.seed(42)
idx       = np.random.permutation(len(X))
split     = int(0.8 * len(X))
X_treino  = X[idx[:split]]
y_treino  = y[idx[:split]]
X_teste   = X[idx[split:]]
y_teste   = y[idx[split:]]

print(f"\n--- Antes do SMOTE ---")
print(f"Treino: {len(X_treino)} amostras | Positivos: {int(y_treino.sum())}")
print(f"Teste : {len(X_teste)} amostras  | Positivos: {int(y_teste.sum())}")

# Aplica SMOTE só no treino
X_treino_smote, y_treino_smote = smote(X_treino, y_treino, desired_ratio=0.20)

print(f"\n--- Depois do SMOTE (só treino) ---")
print(f"Treino: {len(X_treino_smote)} amostras | Positivos: {int(y_treino_smote.sum())}")
print(f"Teste : {len(X_teste)} amostras  | Positivos: {int(y_teste.sum())} (original, sem sintéticos)")

# Salva os dois CSVs separados
cols = df.drop(columns=['depression_label']).columns.tolist()

treino_df = pd.DataFrame(X_treino_smote, columns=cols)
treino_df['depression_label'] = y_treino_smote.astype(int)
treino_df.to_csv("data/treino.csv", index=False)

teste_df = pd.DataFrame(X_teste, columns=cols)
teste_df['depression_label'] = y_teste.astype(int)
teste_df.to_csv("data/teste.csv", index=False)

print("\nArquivos salvos: data/treino.csv e data/teste.csv")