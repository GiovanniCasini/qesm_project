import pandas as pd
import matplotlib.pyplot as plt

# Carica i dati dal file CSV
data = pd.read_csv("results.csv")

# Grafico per la media di successo
plt.figure(figsize=(12, 6))
plt.plot(data['NumberOfClients'], data['AverageSuccessGreedyDistance'], label='Greedy Distance', marker='o')
plt.plot(data['NumberOfClients'], data['AverageSuccessGreedyCDF'], label='Greedy CDF', marker='o')
plt.plot(data['NumberOfClients'], data['AverageSuccessRandom'], label='Random', marker='o')
plt.title('Average Success vs Number of Clients')
plt.xlabel('Number of Clients')
plt.ylabel('Average Success')
plt.legend()
plt.grid()
plt.savefig("average_success_comparison.png")
plt.show()

# Grafico per il costo medio
plt.figure(figsize=(12, 6))
plt.plot(data['NumberOfClients'], data['AverageCostGreedyDistance'], label='Greedy Distance', marker='o')
plt.plot(data['NumberOfClients'], data['AverageCostGreedyCDF'], label='Greedy CDF', marker='o')
plt.plot(data['NumberOfClients'], data['AverageCostRandom'], label='Random', marker='o')
plt.title('Average Cost vs Number of Clients')
plt.xlabel('Number of Clients')
plt.ylabel('Average Cost')
plt.legend()
plt.grid()
plt.savefig("average_cost_comparison.png")
plt.show()
