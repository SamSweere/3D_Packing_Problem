package PackingProblem.Algorithms;

import PackingProblem.Model.ParcelProblem;

public interface IParcelSolver {
    double getTotalValue();
    void solve(ParcelProblem parcelProblem);
}